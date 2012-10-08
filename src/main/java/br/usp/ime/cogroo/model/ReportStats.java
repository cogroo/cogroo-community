package br.usp.ime.cogroo.model;

import java.util.List;

import br.usp.ime.cogroo.logic.errorreport.ErrorEntryLogic;
import br.usp.ime.cogroo.model.errorreport.ErrorEntry;
import br.usp.ime.cogroo.model.errorreport.GrammarCheckerBadIntervention;
import br.usp.ime.cogroo.model.errorreport.GrammarCheckerOmission;

public class ReportStats extends AbstractRuleStatus {

  private int ok = 0, notOK = 0, warn = 0, invalid = 0;

  public ReportStats(List<ErrorEntry> errorEntries) {

    for (ErrorEntry error : errorEntries) {
      String flag = error.getStatusFlag();

      GrammarCheckerBadIntervention badIntervention = error
          .getBadIntervention();

      if (badIntervention != null) {

        if (flag.equals(ErrorEntryLogic.STATUS_OK)) {
          ok++;
        } else {
          if (flag.equals(ErrorEntryLogic.STATUS_WARN)) {
            warn++;
          } else {
            if (flag.equals(ErrorEntryLogic.STATUS_NOT)) {
              notOK++;
              fp++;
            } else {
              invalid++;
            }
          }
        }
      } else {
        GrammarCheckerOmission omission = error.getOmission();
        
        if (omission != null) {

          if (flag.equals(ErrorEntryLogic.STATUS_OK)) {
            ok++;
            tp++;
          } else {
            if (flag.equals(ErrorEntryLogic.STATUS_WARN)) {
              warn++;
            } else {
              if (flag.equals(ErrorEntryLogic.STATUS_NOT)) {
                notOK++;
                fn++;
              } else {
                invalid++;
              }
            }
          }
        }
      }
    }
  }

  public int getOk() {
    return ok;
  }

  public int getNotOK() {
    return notOK;
  }

  public int getWarn() {
    return warn;
  }

  public int getInvalid() {
    return invalid;
  }

}
