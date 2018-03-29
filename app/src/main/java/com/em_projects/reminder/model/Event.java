package com.em_projects.reminder.model;

import java.io.Serializable;

/**
 * Created by eyalmuchtar on 12/19/17.
 */

public class Event implements Serializable {

    private String id;
    private String subject;
    private long timeBeforeSec;      // seconds Alert before the event
    private long startDate;         // Date and time as milis
    private long duration;          // As milis  (includes all day)
    private String repeatType;      // Can be index of emun
    private String animationName;   // The animation's file name
    //private int numberOfAlerts;     // How many time the alert will appears (-100 = infinity)
    private long alertsInterval;    // Time between alerts as milis
    private String tuneName;        // The tune file name

    public enum RepeatType {NONE, DAILY, WEEKLY, MONTHLY, YEARLY}

    public Event(String id, String subject, long startDate, long duration, long timeBeforeSec, RepeatType repeatType,
                 String animationName, /*int numberOfAlerts,*/ long alertsInterval, String tuneName) {
        this.id = id;
        this.subject = subject;
        this.startDate = startDate;
        this.duration = duration;
        this.timeBeforeSec = timeBeforeSec;
        this.repeatType = repeatType.name();
        this.animationName = animationName;
//        this.numberOfAlerts = numberOfAlerts;
        this.alertsInterval = alertsInterval;
        this.tuneName = tuneName;
    }

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public long getStartDate() {
        return startDate;
    }
    public void setStartDate(long newStartTime) {
        startDate = newStartTime;
    }

    public long getDuration() {
        return duration;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public String getAnimationName() {
        return animationName;
    }

//    public int getNumberOfAlerts() {
//        return numberOfAlerts;
//    }


    public long getTimeBeforeSec() {
        return timeBeforeSec;
    }

    public void setTimeBeforeSec(long timeBeforeSec) {
        this.timeBeforeSec = timeBeforeSec;
    }

    public long getAlertsInterval() {
        return alertsInterval;
    }

    public String getTuneName() {
        return tuneName;
    }

    public static RepeatType getRepeatType(String repeatTypeName){
        if (RepeatType.NONE.equals(repeatTypeName)) return RepeatType.NONE;
        else if (RepeatType.DAILY.equals(repeatTypeName)) return RepeatType.DAILY;
        else if (RepeatType.WEEKLY.equals(repeatTypeName)) return RepeatType.WEEKLY;
        else if (RepeatType.MONTHLY.equals(repeatTypeName)) return RepeatType.MONTHLY;
        else if (RepeatType.YEARLY.equals(repeatTypeName)) return RepeatType.YEARLY;
        else return RepeatType.NONE;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", subject='" + subject + '\'' +
                ", timeBeforeSec=" + timeBeforeSec +
                ", startDate=" + startDate +
                ", duration=" + duration +
                ", repeatType='" + repeatType + '\'' +
                ", animationName='" + animationName + '\'' +
                ", alertsInterval=" + alertsInterval +
                ", tuneName='" + tuneName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (timeBeforeSec != event.timeBeforeSec) return false;
        if (startDate != event.startDate) return false;
        if (duration != event.duration) return false;
        if (alertsInterval != event.alertsInterval) return false;
        if (!id.equals(event.id)) return false;
        if (!subject.equals(event.subject)) return false;
        if (!repeatType.equals(event.repeatType)) return false;
        if (!animationName.equals(event.animationName)) return false;
        return tuneName.equals(event.tuneName);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + subject.hashCode();
        result = 31 * result + (int) (timeBeforeSec ^ (timeBeforeSec >>> 32));
        result = 31 * result + (int) (startDate ^ (startDate >>> 32));
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        result = 31 * result + repeatType.hashCode();
        result = 31 * result + animationName.hashCode();
        result = 31 * result + (int) (alertsInterval ^ (alertsInterval >>> 32));
        result = 31 * result + tuneName.hashCode();
        return result;
    }
}
