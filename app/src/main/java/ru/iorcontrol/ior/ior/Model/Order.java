package ru.iorcontrol.ior.ior.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by me on 24/11/2017.
 */

public class Order implements Serializable {

    public String _id;
    public String number;
    public long date;
    public long updated;
    public OrderType type;
    public AssignedTo assignedTo;
    public Egroup assignedToGroup;
    public String comment;
    public String currentstatus;
    public CreatedBy createdBy;
    public String recipientmail;
    public String recipientphone;
    public Client client;
    public List<Status> statuses;
    public String cancelReason;
    public Boolean isFav;
    public List<Discussion> discussion;
    public List<ChatMessage> messages;

    public String getResponsible() {
        if (this.getAssignedTo() == null) {
            return this.assignedToGroup.name;
        } else {
            return this.getAssignedTo().name;
        }
    }

    public String responsible;


    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public String get_id() {
        return _id;
    }

    public String getNumber() {
        return number;
    }

    public long getDate() {
        return date;
    }

    public long getUpdated() {
        return updated;
    }

    public OrderType getType() {
        return type;
    }

    public AssignedTo getAssignedTo() {
        return assignedTo;
    }

    public String getComment() {
        return comment;
    }

    public String getCurrentstatus() {
        return currentstatus;
    }

    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public String getRecipientmail() {
        return recipientmail;
    }

    public String getRecipientphone() {
        return recipientphone;
    }

    public Client getClient() {
        return client;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public Boolean getFav() {
        return isFav;
    }

    public List<Discussion> getDiscussion() {
        return discussion;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public void setAssignedTo(AssignedTo assignedTo) {
        this.assignedTo = assignedTo;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCurrentstatus(String currentstatus) {
        this.currentstatus = currentstatus;
    }

    public void setCreatedBy(CreatedBy createdBy) {
        this.createdBy = createdBy;
    }

    public void setRecipientmail(String recipientmail) {
        this.recipientmail = recipientmail;
    }

    public void setRecipientphone(String recipientphone) {
        this.recipientphone = recipientphone;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public void setFav(Boolean fav) {
        isFav = fav;
    }

    public void setDiscussion(List<Discussion> discussion) {
        this.discussion = discussion;
    }

    public Order(String _id, String number, long date, long updated, OrderType type, AssignedTo assignedTo, String comment, String currentstatus, CreatedBy createdBy, String recipientmail, String recipientphone, Client client, List<Status> statuses, String cancelReason, Boolean isFav, List<Discussion> discussion) {

        this._id = _id;
        this.number = number;
        this.date = date;
        this.updated = updated;
        this.type = type;
        this.assignedTo = assignedTo;
        this.comment = comment;
        this.currentstatus = currentstatus;
        this.createdBy = createdBy;
        this.recipientmail = recipientmail;
        this.recipientphone = recipientphone;
        this.client = client;
        this.statuses = statuses;
        this.cancelReason = cancelReason;
        this.isFav = isFav;
        this.discussion = discussion;
    }
}
