import java.util.*;
public class SATSolver {
    public static class SATResult {
        public final boolean satisfiable;
        public final Map<Integer, Boolean> model;
        public SATResult(boolean satisfiable, Map<Integer, Boolean> model) {
            this.satisfiable = satisfiable;
            this.model = model;
        }
    }
    public static SATResult solve(int numVars, List<List<Integer>> clauses) {
        Boolean[] assignment = new Boolean[numVars + 1];
        boolean solved = dpll(1, numVars, clauses, assignment);
        if (solved) {
            Map<Integer, Boolean> model = new LinkedHashMap<>();
            for (int i = 1; i <= numVars; i++) {
                model.put(i, assignment[i] != null && assignment[i]);
            }
            return new SATResult(true, model);
        }
        return new SATResult(false, Collections.emptyMap());
    }
    private static boolean dpll(int varIndex, int numVars, List<List<Integer>> clauses, Boolean[] assignment) {
        int status = checkFormula(clauses, assignment);
        if (status == 1) return true;
        if (status == -1) return false;
        if (varIndex > numVars) {
            return checkFormula(clauses, assignment) == 1;
        }
        assignment[varIndex] = true;
        if (dpll(varIndex + 1, numVars, clauses, assignment)) return true;
        assignment[varIndex] = false;
        if (dpll(varIndex + 1, numVars, clauses, assignment)) return true;
        assignment[varIndex] = null;
        return false;
    }
    private static int checkFormula(List<List<Integer>> clauses, Boolean[] assignment) {
        boolean allSatisfied = true;
        for (List<Integer> clause : clauses) {
            boolean clauseSat = false;
            boolean hasUnassigned = false;
            for (int lit : clause) {
                int var = Math.abs(lit);
                boolean sign = lit > 0;
                if (assignment[var] != null) {
                    if (assignment[var] == sign) {
                        clauseSat = true;
                        break;
                    }
                } else {
                    hasUnassigned = true;
                }
            }
            if (!clauseSat) {
                if (!hasUnassigned) {
                    return -1;
                }
                allSatisfied = false;
            }
        }
        return allSatisfied ? 1 : 0;
    }
}
