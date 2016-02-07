package com.codeweb.rem.model;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadStackTraceEntries
{
  private final long dtg;
  private final List<StackTraceEntry> entries = new ArrayList<>();

  public MultiThreadStackTraceEntries(long dtg)
  {
    super();
    this.dtg = dtg;
  }

  public long getDtg()
  {
    return dtg;
  }

  public void addEntry(StackTraceEntry entry)
  {
    this.entries.add(entry);
  }

  public List<StackTraceEntry> getEntries()
  {
    return entries;
  }
}
