package com.krunal.choosedirectory.Adapter;
import android.content.Context;
import android.os.StatFs;
import android.view.View;
import android.widget.TextView;
import com.krunal.choosedirectory.ClsGlobal.SpaceFormatter;
import com.krunal.choosedirectory.R;
public class StorageAdapter extends BaseListAdapter<String, StorageAdapter.ViewHolder>
{
    public StorageAdapter(Context context)
    {
        super(context, R.layout.row_storage);
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

    }
    @Override
    protected ViewHolder viewHolder(View view)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        return new ViewHolder(view);
    }
    @Override
    protected void fillView(View rowView, ViewHolder viewHolder, String item)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        viewHolder.name.setText(item);

        try {
            StatFs stat = new StatFs(item);
            long blockSize = (long) stat.getBlockSize();
            long totalSpace = blockSize * stat.getBlockCount();
            long availableSpace = blockSize * stat.getAvailableBlocks();
            SpaceFormatter spaceFormatter = new SpaceFormatter();
            String labelTotal = getContext().getString(R.string.space_total);
            String total = spaceFormatter.format(totalSpace);
            String labelAvailable = getContext().getString(R.string.space_available);
            String available = spaceFormatter.format(availableSpace);
            viewHolder.space.setText(String.format("%s: %s     %s: %s",
                                                   labelTotal, total, labelAvailable, available));
        } catch (Exception e) {
        }
    }
    protected static class ViewHolder
    {
        public final TextView name;
        public final TextView space;
        public ViewHolder(View view)
        {
            System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

            this.name = view.findViewById(R.id.name);
            this.space = view.findViewById(R.id.space);
        }
    }
}
