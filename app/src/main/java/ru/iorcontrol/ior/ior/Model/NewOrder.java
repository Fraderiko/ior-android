package ru.iorcontrol.ior.ior.Model;

import java.util.List;

/**
 * Created by alexeykazinets on 30/11/2017.
 */

public class NewOrder {

    public String number;
    public long date;
    public long updated;
    public String type;
    public String currentstatus;
    public String assignedTo;
    public String assignedToGroup;
    public String comment;
    public List<StatusTemplate> statuses;
    public String createdBy;
    public String group;
    public String recipientmail;
    public String recipientphone;
    public String client;
    public String cancelReason;
    public List<Discussion> discussion;
    public Boolean isArchived;

    public void setNumber(String number) {

        this.number = number;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCurrentstatus(String currentstatus) {
        this.currentstatus = currentstatus;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setStatuses(List<StatusTemplate> statuses) {
        this.statuses = statuses;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setRecipientmail(String recipientmail) {
        this.recipientmail = recipientmail;
    }

    public void setRecipientphone(String recipientphone) {
        this.recipientphone = recipientphone;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public void setDiscussion(List<Discussion> discussion) {
        this.discussion = discussion;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }
}
