package com.codeweb.rem.model;

import java.util.ArrayList;
import java.util.List;

public class StackTraceRecordedExecution
{
  private final String projName;
  private final long startDtg;
  private final List<StackTraceTimedRecording> recordings = new ArrayList<>();

  public StackTraceRecordedExecution(String name, long start)
  {
    super();
    this.projName = name;
    this.startDtg = start;
  }

  public String getProjName()
  {
    return projName;
  }

  public long getStartDtg()
  {
    return startDtg;
  }

  public void addRecording(StackTraceTimedRecording record)
  {
    this.recordings.add(record);
  }

  public List<StackTraceTimedRecording> getRecordings()
  {
    return recordings;
  }

}
