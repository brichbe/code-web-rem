package com.codeweb.rem.model;

import java.util.ArrayList;
import java.util.List;

public class StackTraceExecution
{
  private final String projName;
  private final long startDtg;
  private final List<MultiThreadStackTraceEntries> entries = new ArrayList<>();

  public StackTraceExecution(String name, long start)
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

  public void addEntry(MultiThreadStackTraceEntries entry)
  {
    this.entries.add(entry);
  }

  public List<MultiThreadStackTraceEntries> getEntries()
  {
    return entries;
  }

}
