package me.loki2302.semantics.expressions;

import me.loki2302.semantics.types.Type;

public class AddIntsExpression implements Expression {
    private final Type intType;
    private final Expression leftExpression;
    private final Expression rightExpression;
    
    public AddIntsExpression(Type intType, Expression leftExpression, Expression rightExpression) {
        this.intType = intType;
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }
    
    public Expression getLeftExpression() {
        return leftExpression;
    }
    
    public Expression getRightExpression() {
        return rightExpression;
    }

    @Override
    public Type getType() {
        return intType;
    }
    
    @Override
    public String toString() {
        return String.format("iadd(%s,%s)", leftExpression, rightExpression);
    }

    @Override
    public <TResult> TResult accept(ExpressionVisitor<TResult> visitor) {
        return visitor.visit(this);
    }
}