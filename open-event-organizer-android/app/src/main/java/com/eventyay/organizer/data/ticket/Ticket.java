package com.eventyay.organizer.data.ticket;

import com.eventyay.organizer.data.db.configuration.OrgaDatabase;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.order.Order;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Delegate;

@Data
@Builder
@Type("ticket")
@AllArgsConstructor
@ToString(exclude = "event")
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
@Table(database = OrgaDatabase.class, allFields = true)
@EqualsAndHashCode(exclude = {"ticketDelegate"})
@SuppressWarnings("PMD.TooManyFields")
public class Ticket implements Comparable<Ticket> {

    @Delegate
    private final TicketDelegateImpl ticketDelegate = new TicketDelegateImpl(this);

    @Id(LongIdHandler.class)
    @PrimaryKey
    public Long id;

    public String description;
    public String type;
    public Float price;
    public String name;
    public Integer maxOrder;
    public Boolean isDescriptionVisible;
    public Boolean isFeeAbsorbed;
    public Integer position;
    public Long quantity;
    public Boolean isHidden;
    public String salesStartsAt;
    public String salesEndsAt;
    public Integer minOrder;
    public boolean isCheckinRestricted;
    public boolean autoCheckinEnabled;

    @Relationship("event")
    @ForeignKey(stubbedRelationship = true, onDelete = ForeignKeyAction.CASCADE)
    public Event event;

    @Relationship("order")
    @ForeignKey(stubbedRelationship = true, onDelete = ForeignKeyAction.CASCADE)
    public Order order;

    public Ticket() {
    }
}
