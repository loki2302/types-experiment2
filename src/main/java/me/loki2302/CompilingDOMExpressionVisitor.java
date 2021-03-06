package me.loki2302;

import me.loki2302.semantics.Compiler;
import me.loki2302.semantics.expressions.Expression;
import me.loki2302.semantics.requests.MakeDoubleConstExpressionRequest;
import me.loki2302.semantics.requests.MakeIntConstExpressionRequest;
import me.loki2302.semantics.requests.MakeOperatorAddExpressionRequest;
import me.loki2302.syntax.dom.expressions.DOMBinaryExpression;
import me.loki2302.syntax.dom.expressions.DOMBinaryExpressionType;
import me.loki2302.syntax.dom.expressions.DOMExplicitCastExpression;
import me.loki2302.syntax.dom.expressions.DOMExpressionVisitor;
import me.loki2302.syntax.dom.expressions.DOMFunctionCallExpression;
import me.loki2302.syntax.dom.expressions.DOMLiteralExpression;
import me.loki2302.syntax.dom.expressions.DOMLiteralType;
import me.loki2302.syntax.dom.expressions.DOMUnaryExpression;
import me.loki2302.syntax.dom.expressions.DOMVariableReferenceExpression;

public class CompilingDOMExpressionVisitor implements DOMExpressionVisitor<Expression> {
    private final Compiler compiler;
    
    public CompilingDOMExpressionVisitor(Compiler compiler) {
        this.compiler = compiler;
    }
    
    @Override
    public Expression visit(DOMBinaryExpression e) {
        if(!e.getExpressionType().equals(DOMBinaryExpressionType.Add)) {
            throw new RuntimeException("Not implemented"); 
        }
        
        Expression leftExpression = e.getLeftExpression().accept(this);
        Expression rightExpression = e.getRightExpression().accept(this);
        return compiler.compile(new MakeOperatorAddExpressionRequest(leftExpression, rightExpression));
    }

    @Override
    public Expression visit(DOMExplicitCastExpression e) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Expression visit(DOMFunctionCallExpression e) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Expression visit(DOMLiteralExpression e) {
        if(e.getLiteralType().equals(DOMLiteralType.Int)) {
            return compiler.compile(new MakeIntConstExpressionRequest(e.getStringValue()));
        } else if(e.getLiteralType().equals(DOMLiteralType.Double)) {
            return compiler.compile(new MakeDoubleConstExpressionRequest(e.getStringValue()));
        }
        
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Expression visit(DOMUnaryExpression e) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Expression visit(DOMVariableReferenceExpression e) {
        throw new RuntimeException("Not implemented");
    }
}