package org.metrics;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * A visitor class that calculates the cyclomatic complexity of a Java method.
 */
public class CyclomaticComplexityVisitor extends VoidVisitorAdapter<Void> {
    private int decisionPoint = 0;
    private int exitPoint = 0;
    private boolean isNonVoidMethod = false;

    @Override
    public void visit(MethodDeclaration method, Void arg) {
        isNonVoidMethod = !method.getType().isVoidType();
        super.visit(method, arg);
    }

    @Override
    public void visit(IfStmt stmt, Void arg) {
        decisionPoint++;
        super.visit(stmt, arg);
    }

    @Override
    public void visit(SwitchStmt stmt, Void arg) {
        decisionPoint += stmt.getEntries().size();
        decisionPoint -= stmt.getEntries().stream().anyMatch(entry -> entry.getLabels().isEmpty()) ? 1 : 0;
        super.visit(stmt, arg);
    }

    @Override
    public void visit(ForStmt stmt, Void arg) {
        decisionPoint++;
        super.visit(stmt, arg);
    }

    @Override
    public void visit(WhileStmt stmt, Void arg) {
        decisionPoint++;
        super.visit(stmt, arg);
    }

    @Override
    public void visit(TryStmt stmt, Void arg) {
        decisionPoint += stmt.getCatchClauses().size();
        decisionPoint += stmt.getFinallyBlock().isPresent() ? 1 : 0;
        super.visit(stmt, arg);
    }

    @Override
    public void visit(ConditionalExpr expr, Void arg) {
        decisionPoint++;
        super.visit(expr, arg);
    }

    @Override
    public void visit(ReturnStmt stmt, Void arg) {
        exitPoint++;
        super.visit(stmt, arg);
    }

    public int getComplexity() {
        if (isNonVoidMethod && exitPoint > 0) {
            return decisionPoint - exitPoint + 2;
        }
        // Void method: one implicit return-exit point: d - 1 + 1 = d + 1
        return decisionPoint + 1;
    }

    public int getDecisionPoint() {
        return decisionPoint;
    }

    public int getExitPoint() {
        if (isNonVoidMethod) {
            return exitPoint;
        } else {
            return 1;
        }
    }
}
