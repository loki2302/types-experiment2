package me.loki2302.syntax.dom.expressions;

public class DOMBinaryExpression extends DOMExpression {
	private final DOMBinaryExpressionType expressionType;
	private final DOMExpression leftExpression;
	private final DOMExpression rightExpression;
	
	public DOMBinaryExpression(
			DOMBinaryExpressionType expressionType,
			DOMExpression leftExpression, 
			DOMExpression rightExpression) {
		
		this.expressionType = expressionType;
		this.leftExpression = leftExpression;
		this.rightExpression = rightExpression;
	}
	
	public DOMBinaryExpressionType getExpressionType() {
		return expressionType;
	}
	
	public DOMExpression getLeftExpression() {
		return leftExpression;
	}
	
	public DOMExpression getRightExpression() {
		return rightExpression;
	}

    @Override
    public <TResult> TResult accept(DOMExpressionVisitor<TResult> visitor) {
        return visitor.visit(this);
    }
}