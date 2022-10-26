package edu.wpi.DapperDaemons.controllers.helpers;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class FuzzySearchComparatorMethod implements AutoCompleteFuzzy.AutoCompleteComparator {

  @Override
  public boolean matches(String typedText, Object objectToCompare) {
    return FuzzySearch.weightedRatio(typedText, objectToCompare.toString()) > 70;
  }
}
