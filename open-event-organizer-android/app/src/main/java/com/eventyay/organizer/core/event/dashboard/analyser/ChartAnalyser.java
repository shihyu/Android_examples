package com.eventyay.organizer.core.event.dashboard.analyser;

import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;

import com.eventyay.organizer.R;
import com.eventyay.organizer.data.ContextUtils;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.attendee.AttendeeRepositoryImpl;
import com.eventyay.organizer.data.order.Order;
import com.eventyay.organizer.utils.DateUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.reactivex.Completable;
import io.reactivex.Observable;

@SuppressWarnings("PMD.TooManyFields")
public class ChartAnalyser {

    private static final int TICKET_SALE_THRESHOLD = 5;

    private final ContextUtils utilModel;
    private final AttendeeRepositoryImpl attendeeRepository;

    private final Map<String, Long> freeMap = new ConcurrentHashMap<>();
    private final Map<String, Long> paidMap = new ConcurrentHashMap<>();
    private final Map<String, Long> donationMap = new ConcurrentHashMap<>();
    private final Map<Integer, Long> checkInTimeMap = new ConcurrentHashMap<>();

    private final LineData lineData = new LineData();

    private LineDataSet freeSet;
    private LineDataSet paidSet;
    private LineDataSet donationSet;
    private LineDataSet checkInDataSet;

    private long maxTicketSale;

    private List<Attendee> attendees;
    private boolean error;
    private boolean isCheckinChart;
    String[] values = new String[]{"00:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00",
        "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00",
        "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"};

    @Inject
    protected ChartAnalyser(ContextUtils utilModel, AttendeeRepositoryImpl attendeeRepository) {
        this.utilModel = utilModel;
        this.attendeeRepository = attendeeRepository;

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#992ecc71"));
        gridPaint.setStrokeWidth(5);
    }

    public void reset() {
        clearData();
        if (attendees != null) attendees.clear();
    }

    private void clearData() {
        error = false;
        freeMap.clear();
        paidMap.clear();
        donationMap.clear();
        checkInTimeMap.clear();

        lineData.clearValues();
        maxTicketSale = 0;
    }

    private Observable<Attendee> getAttendeeSource(long eventId) {
        if (attendees == null || attendees.isEmpty())
            return attendeeRepository.getAttendees(eventId, false);
        else
            return Observable.fromIterable(attendees);
    }

    public Completable loadData(long eventId) {
        clearData();
        isCheckinChart = false;
        return getAttendeeSource(eventId)
            .doOnNext(attendee -> {
                Order order = attendee.getOrder();
                if (order == null) {
                    error = true;
                    return;
                }

                String date = order.getCompletedAt();
                switch (attendee.getTicket().getType()) {
                    case TicketAnalyser.TICKET_FREE:
                        addDataPointForSales(freeMap, date);
                        break;
                    case TicketAnalyser.TICKET_DONATION:
                        addDataPointForSales(donationMap, date);
                        break;
                    case TicketAnalyser.TICKET_PAID:
                        addDataPointForSales(paidMap, date);
                        break;
                    default:
                        // No action
                }
            })
            .toList()
            .doAfterSuccess(attendees -> this.attendees = attendees)
            .toCompletable()
            .doOnComplete(() -> {
                if (error) throw new IllegalAccessException("No order found");
                normalizeDataSet();
                freeSet = setDataForSales(freeMap, "Free");
                paidSet = setDataForSales(paidMap, "Paid");
                donationSet = setDataForSales(donationMap, "Donation");
                prepare();
            });
    }

    public Completable loadDataCheckIn(long eventId) {
        clearData();
        isCheckinChart = true;
        return getAttendeeSource(eventId).doOnNext(attendee -> {
            String checkInTime = attendee.getCheckinTimes();
            int length = checkInTime.split(",").length;
            String latestCheckInTime = checkInTime.split(",")[length - 1];
            error = checkInTime == null ? true : false;
            addDataPointForCheckIn(checkInTimeMap, latestCheckInTime);
        })
            .toList()
            .doAfterSuccess(attendees -> this.attendees = attendees)
            .toCompletable()
            .doOnComplete(() -> {
                if (error)
                    throw new IllegalAccessException("No checkin's found");
                checkInDataSet = setDataForCheckIn(checkInTimeMap, "check-in time");
                prepare();
            });
    }

    private void putIfNotPresent(Map<String, Long> map, String key) {
        if (!map.containsKey(key))
            map.put(key, 0L);
    }

    private void normalizeDataSet(Map<String, Long> source, Map<String, Long> other1, Map<String, Long> other2) {
        for (Map.Entry<String, Long> entry : source.entrySet()) {
            putIfNotPresent(other1, entry.getKey());
            putIfNotPresent(other2, entry.getKey());
        }
    }

    private void normalizeDataSet() {
        normalizeDataSet(freeMap, paidMap, donationMap);
        normalizeDataSet(paidMap, freeMap, donationMap);
        normalizeDataSet(donationMap, paidMap, freeMap);
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    // Entries cannot be created outside loop
    private LineDataSet setDataForSales(Map<String, Long> map, String label) throws ParseException {
        List<Entry> entries = new ArrayList<>();
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            String date = DateUtils.formatDateWithDefault(DateUtils.FORMAT_DAY_COMPLETE, entry.getKey());
            float time = DateUtils.getDate(entry.getKey()).toEpochSecond();

            entries.add(new Entry(time, entry.getValue(), date));
        }
        Collections.sort(entries, new EntryXComparator());

        // Add a starting date point ine day ago
        float dayMillis = 60 * 60 * 24 * 1000;
        entries.add(0, new Entry(entries.get(0).getX() - dayMillis, 0));
        return new LineDataSet(entries, label);
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    // Entries cannot be created outside loop
    private LineDataSet setDataForCheckIn(Map<Integer, Long> map, String label) throws ParseException {
        List<Entry> entries = new ArrayList<>();
        for (Map.Entry<Integer, Long> entry : map.entrySet()) {
            entries.add(new Entry(entry.getKey(), entry.getValue()));
        }
        Collections.sort(entries, new EntryXComparator());

        // Add a starting point 2 hrs ago
        entries.add(0, new Entry(entries.get(0).getX() - 2, 0));
        return new LineDataSet(entries, label);
    }

    private void addDataPointForSales(Map<String, Long> map, String dateString) {
        Long amount = map.get(dateString);
        if (amount == null)
            amount = 0L;
        ++amount;

        if (amount > maxTicketSale)
            maxTicketSale = amount;
        map.put(dateString, amount);
    }

    private void addDataPointForCheckIn(Map<Integer, Long> map, String dateString) {
        int hour = DateUtils.getDate(dateString).getHour();
        Long numberOfCheckins = map.get(hour);

        if (numberOfCheckins == null)
            numberOfCheckins = 0L;

        map.put(hour, ++numberOfCheckins);
    }

    @ColorInt
    private int getColor(@ColorRes int colorId) {
        return utilModel.getResourceColor(colorId);
    }

    private void initializeLineSet(LineDataSet lineSet, @ColorRes int color, @ColorRes int fill) {
        lineSet.setLineWidth(2);
        lineSet.setColor(getColor(color));
        lineSet.setCircleColor(getColor(color));
        lineSet.setCircleHoleColor(getColor(fill));
        lineSet.setCircleRadius(8);
        lineSet.setCircleHoleRadius(3);
    }

    private void prepare() {
        if (isCheckinChart) {
            initializeLineSet(checkInDataSet, R.color.light_blue_500, R.color.light_blue_100);
            lineData.addDataSet(checkInDataSet);
        } else {
            initializeLineSet(freeSet, R.color.light_blue_500, R.color.light_blue_100);
            initializeLineSet(paidSet, R.color.purple_500, R.color.purple_100);
            initializeLineSet(donationSet, R.color.red_500, R.color.red_100);
            lineData.addDataSet(freeSet);
            lineData.addDataSet(paidSet);
            lineData.addDataSet(donationSet);
            lineData.setDrawValues(false);
        }
        lineData.setDrawValues(false);
    }

    @SuppressFBWarnings(
        value = "ICAST_IDIV_CAST_TO_DOUBLE",
        justification = "We want granularity to be integer")
    public void showChart(LineChart lineChart) {
        lineChart.setData(lineData);
        lineChart.getXAxis().setEnabled(true);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setGridLineWidth(1);
        yAxis.setGridColor(Color.parseColor("#992ecc71"));
        if (!isCheckinChart)
            if (maxTicketSale > TICKET_SALE_THRESHOLD)
                yAxis.setGranularity(maxTicketSale / TICKET_SALE_THRESHOLD);
            else {
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setValueFormatter(new MyAxisValueFormatter());
                yAxis.setGranularity(1);
                lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                lineChart.getXAxis().setGranularity(1f);
            }

        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);
        lineChart.animateY(1000);
    }

    public class MyAxisValueFormatter extends ValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if (value < 0)
                return values[24 + (int) value];
            return values[(int) value];
        }
    }
}
