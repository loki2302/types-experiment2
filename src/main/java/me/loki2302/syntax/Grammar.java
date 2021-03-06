package me.loki2302.syntax;

import java.util.ArrayList;
import java.util.List;

import me.loki2302.syntax.dom.DOMElement;
import me.loki2302.syntax.dom.expressions.DOMBinaryExpression;
import me.loki2302.syntax.dom.expressions.DOMBinaryExpressionType;
import me.loki2302.syntax.dom.expressions.DOMExplicitCastExpression;
import me.loki2302.syntax.dom.expressions.DOMExpression;
import me.loki2302.syntax.dom.expressions.DOMFunctionCallExpression;
import me.loki2302.syntax.dom.expressions.DOMLiteralExpression;
import me.loki2302.syntax.dom.expressions.DOMLiteralType;
import me.loki2302.syntax.dom.expressions.DOMUnaryExpression;
import me.loki2302.syntax.dom.expressions.DOMUnaryExpressionType;
import me.loki2302.syntax.dom.expressions.DOMVariableReferenceExpression;
import me.loki2302.syntax.dom.functions.DOMFunctionDefinition;
import me.loki2302.syntax.dom.functions.DOMParameterDefinition;
import me.loki2302.syntax.dom.programs.DOMProgram;
import me.loki2302.syntax.dom.statements.DOMBreakStatement;
import me.loki2302.syntax.dom.statements.DOMCompositeStatement;
import me.loki2302.syntax.dom.statements.DOMContinueStatement;
import me.loki2302.syntax.dom.statements.DOMDoWhileStatement;
import me.loki2302.syntax.dom.statements.DOMExpressionStatement;
import me.loki2302.syntax.dom.statements.DOMForStatement;
import me.loki2302.syntax.dom.statements.DOMIfStatement;
import me.loki2302.syntax.dom.statements.DOMNullStatement;
import me.loki2302.syntax.dom.statements.DOMReturnStatement;
import me.loki2302.syntax.dom.statements.DOMStatement;
import me.loki2302.syntax.dom.statements.DOMVariableDefinitionStatement;
import me.loki2302.syntax.dom.statements.DOMWhileStatement;
import me.loki2302.syntax.dom.types.DOMNamedTypeReference;
import me.loki2302.syntax.dom.types.DOMTypeReference;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.support.StringVar;
import org.parboiled.support.Var;

public class Grammar extends BaseParser<DOMElement> {    
    public Rule OPEN_PARENTHESIS = TERMINAL("(");
    public Rule CLOSE_PARENTHESIS = TERMINAL(")");
    public Rule COMMA = TERMINAL(",");
    public Rule SEMICOLON = TERMINAL(";");
    public Rule OPEN_BRACE = TERMINAL("{");
    public Rule CLOSE_BRACE = TERMINAL("}");
    public Rule NOTHING = TERMINAL("");    
    public Rule IF = TERMINAL("if");
    public Rule ELSE = TERMINAL("else");
    public Rule FOR = TERMINAL("for");
    public Rule WHILE = TERMINAL("while");
    public Rule DO = TERMINAL("do");
    public Rule CONTINUE = TERMINAL("continue");
    public Rule BREAK = TERMINAL("break");
    public Rule RETURN = TERMINAL("return");    
    
    public Rule program() {
        Var<ProgramBuilder> builder = new Var<ProgramBuilder>(new ProgramBuilder());
        return Sequence(
                OneOrMore(
                        Sequence(
                                functionDefinition(),
                                ACTION(builder.get().appendFunctionDefinition(popFunctionDefinition())))),
                EOI,
                push(builder.get().build()));
    }
    
    public Rule functionDefinition() {
        Var<FunctionDefinitionBuilder> builder = new Var<FunctionDefinitionBuilder>(new FunctionDefinitionBuilder()); 
        return Sequence(
                Sequence(
                        namedTypeReference(), 
                        ACTION(builder.get().setResultType(popTypeReference()))),
                allowSpaces(Sequence(
                        name(),
                        ACTION(builder.get().setFunctionName(match())))),
                OPEN_PARENTHESIS,
                Optional(
                        Sequence(
                                parameterDefinition(),
                                ACTION(builder.get().appendParameterDefinition(popParameterDefinition())),
                        ZeroOrMore(
                                COMMA,
                                Sequence(
                                    parameterDefinition(),
                                    ACTION(builder.get().appendParameterDefinition(popParameterDefinition())))))),
                CLOSE_PARENTHESIS,
                Sequence(
                        statement(),
                        ACTION(builder.get().setBody(popStatement()))),
                push(builder.get().build()));
    }
    
    public Rule parameterDefinition() {
        Var<ParameterDefinitionBuilder> builder = new Var<ParameterDefinitionBuilder>(new ParameterDefinitionBuilder()); 
        return Sequence(
                Sequence(
                        namedTypeReference(), 
                        ACTION(builder.get().setTypeReference(popTypeReference()))),
                allowSpaces(Sequence(
                        name(),
                        ACTION(builder.get().setParameterName(match())))),
                push(builder.get().build()));
    }
    
    public Rule statement() {
        return FirstOf(
                compositeStatement(),                
                ifStatement(),
                forStatement(),
                whileStatement(),
                Sequence(variableDefinitionStatement(), SEMICOLON),
                Sequence(returnStatement(), SEMICOLON),
                Sequence(continueStatement(), SEMICOLON),
                Sequence(breakStatement(), SEMICOLON),
                Sequence(doWhileStatement(), SEMICOLON),
                Sequence(expressionStatement(), SEMICOLON),                
                Sequence(nullStatement(), SEMICOLON));
    }
    
    public Rule pureStatement() {
        return FirstOf(
                compositeStatement(),
                variableDefinitionStatement(),
                returnStatement(),
                continueStatement(),
                breakStatement(),
                ifStatement(),
                forStatement(),
                whileStatement(),
                doWhileStatement(),
                expressionStatement(),                
                nullStatement());
    }    
    
    public Rule nullStatement() {
        return Sequence(NOTHING, push(new DOMNullStatement()));
    }
    
    public Rule variableDefinitionStatement() {
        Var<VariableDefinitionStatementBuilder> builder = 
                new Var<VariableDefinitionStatementBuilder>(new VariableDefinitionStatementBuilder());
        return Sequence(
                Sequence(
                        namedTypeReference(),
                        ACTION(builder.get().setTypeReference(popTypeReference()))),
                Sequence(
                        name(),
                        ACTION(builder.get().setVariableName(match()))),
                allowSpaces(String("=")),
                Sequence(
                        expression(),
                        ACTION(builder.get().setExpression(popExpression()))),
                push(builder.get().build()));
    }
    
    public Rule namedTypeReference() {
        StringVar typeName = new StringVar();
        return Sequence(
                allowSpaces(
                        Sequence(
                                name(),
                                ACTION(typeName.set(match())))),
                push(new DOMNamedTypeReference(typeName.get())));
    }
    
    public Rule returnStatement() {
        Var<ReturnStatementBuilder> builder = new Var<ReturnStatementBuilder>(new ReturnStatementBuilder());
        return Sequence(
                RETURN,
                Optional(Sequence(
                        expression(),
                        ACTION(builder.get().setExpression(popExpression())))),
                push(builder.get().build()));
    }
    
    public Rule continueStatement() {
        return Sequence(CONTINUE, push(new DOMContinueStatement()));
    }
    
    public Rule breakStatement() {
        return Sequence(BREAK, push(new DOMBreakStatement()));
    }
    
    public Rule forStatement() {
        Var<ForStatementBuilder> builder = new Var<ForStatementBuilder>(new ForStatementBuilder());
        return Sequence(
                FOR,
                OPEN_PARENTHESIS,
                Optional(
                        Sequence(
                                pureStatement(),
                                ACTION(builder.get().setInitializerStatement(popStatement())))),
                SEMICOLON,
                Optional(
                        Sequence(
                                expression(),
                                ACTION(builder.get().setConditionExpression(popExpression())))),
                SEMICOLON,
                Optional(
                        Sequence(
                                pureStatement(),
                                ACTION(builder.get().setStepStatement(popStatement())))),
                CLOSE_PARENTHESIS,
                Sequence(
                        statement(),
                        ACTION(builder.get().setBodyStatement(popStatement()))),
                push(builder.get().build()));
    }
    
    public Rule whileStatement() {
        Var<WhileStatementBuilder> builder = new Var<WhileStatementBuilder>(new WhileStatementBuilder());
        return Sequence(
                WHILE,
                OPEN_PARENTHESIS,
                Sequence(
                        expression(),
                        ACTION(builder.get().setConditionExpression(popExpression()))),
                CLOSE_PARENTHESIS,
                Sequence(
                        statement(),
                        ACTION(builder.get().setBodyStatement(popStatement()))),
                push(builder.get().build()));
    }
    
    public Rule doWhileStatement() {
        Var<DoWhileStatementBuilder> builder = new Var<DoWhileStatementBuilder>(new DoWhileStatementBuilder());
        return Sequence(
                DO,
                Sequence(
                        statement(),
                        ACTION(builder.get().setBodyStatement(popStatement()))),
                WHILE,
                OPEN_PARENTHESIS,
                Sequence(
                        expression(),
                        ACTION(builder.get().setConditionExpression(popExpression()))),
                CLOSE_PARENTHESIS,
                push(builder.get().build()));
    }
    
    public Rule ifStatement() {
        Var<IfStatementBuilder> builder = new Var<IfStatementBuilder>(new IfStatementBuilder());
        return Sequence(
                IF,
                OPEN_PARENTHESIS,
                Sequence(
                        expression(),
                        ACTION(builder.get().setConditionExpression(popExpression())),
                CLOSE_PARENTHESIS,
                Sequence(
                        statement(),
                        ACTION(builder.get().setTrueBranch(popStatement())),
                Optional(
                        ELSE,
                        Sequence(
                                statement(),
                                ACTION(builder.get().setFalseBranch(popStatement())))))),
                push(builder.get().build()));
    }
    
    public Rule compositeStatement() {
        Var<CompositeStatementBuilder> builder = 
                new Var<CompositeStatementBuilder>(new CompositeStatementBuilder());
        return Sequence(
                OPEN_BRACE,                
                ZeroOrMore(
                        Sequence(
                                statement(),
                                ACTION(builder.get().appendStatement(popStatement())))),
                CLOSE_BRACE,
                push(builder.get().build()));
    }
    
    public Rule expressionStatement() {
        return Sequence(
                expression(),
                push(new DOMExpressionStatement(popExpression())));
    }
    
    public Rule expression() {
        return assignmentExpression();
    }
    
    public Rule parensExpression() {
        return Sequence(
                OPEN_PARENTHESIS,
                expression(),
                CLOSE_PARENTHESIS);
    }
    
    public Rule assignmentExpression() {
        StringVar op = new StringVar();
        return Sequence(
                orExpression(),
                ZeroOrMore(
                        allowSpaces(Sequence(
                                FirstOf("=", "+=", "-=", "*=", "/="),
                                op.set(match()))),                      
                        orExpression(),
                        push(Helper.domBinaryExpressionFromString(
                                op.get(), 
                                (DOMExpression)pop(1), 
                                (DOMExpression)pop()))));
    }
    
    public Rule orExpression() {
        StringVar op = new StringVar();
        return Sequence(
                andExpression(),
                ZeroOrMore(
                        allowSpaces(Sequence(
                                "||",
                                op.set(match()))),                      
                        andExpression(),
                        push(Helper.domBinaryExpressionFromString(
                                op.get(), 
                                (DOMExpression)pop(1), 
                                (DOMExpression)pop()))));
    }
    
    public Rule andExpression() {
        StringVar op = new StringVar();
        return Sequence(
                equalityComparisonExpression(),
                ZeroOrMore(
                        allowSpaces(Sequence(
                                "&&",
                                op.set(match()))),                      
                        equalityComparisonExpression(),
                        push(Helper.domBinaryExpressionFromString(
                                op.get(), 
                                (DOMExpression)pop(1), 
                                (DOMExpression)pop()))));
    }
    
    public Rule equalityComparisonExpression() {
        StringVar op = new StringVar();
        return Sequence(
                comparisonExpression(),
                ZeroOrMore(
                        allowSpaces(Sequence(
                                FirstOf("==", "!="),
                                op.set(match()))),                      
                        comparisonExpression(),
                        push(Helper.domBinaryExpressionFromString(
                                op.get(), 
                                (DOMExpression)pop(1), 
                                (DOMExpression)pop()))));
    }
    
    public Rule comparisonExpression() {
        StringVar op = new StringVar();
        return Sequence(
                addSubExpression(),
                ZeroOrMore(
                        allowSpaces(Sequence(
                                FirstOf("<=", "<", ">=", ">"),
                                op.set(match()))),                      
                        addSubExpression(),
                        push(Helper.domBinaryExpressionFromString(
                                op.get(), 
                                (DOMExpression)pop(1), 
                                (DOMExpression)pop()))));
    }
    
	public Rule addSubExpression() {
		StringVar op = new StringVar();
		return Sequence(
				mulDivExpression(),
				ZeroOrMore(
						allowSpaces(Sequence(
								FirstOf("+", "-"),
								op.set(match()))),						
						mulDivExpression(),
						push(Helper.domBinaryExpressionFromString(
								op.get(), 
								(DOMExpression)pop(1), 
								(DOMExpression)pop()))));
	}
	
	public Rule mulDivExpression() {
		StringVar op = new StringVar();
		return Sequence(
		        unaryExpression(),
				ZeroOrMore(
						allowSpaces(Sequence(
								FirstOf("*", "/"),
								op.set(match()))),
						unaryExpression(),
						push(Helper.domBinaryExpressionFromString(
								op.get(), 
								(DOMExpression)pop(1), 
								(DOMExpression)pop()))));
	}
	
	public Rule unaryExpression() {
	    return FirstOf(
	            prefixIncrementExpression(),
	            postfixIncrementExpression(),
	            prefixDecrementExpression(),
	            postfixDecrementExpression(),
	            plusSignExpression(),
	            minusSignExpression(),
	            notExpression(),
	            factor());
	}
	
	public Rule prefixIncrementExpression() {
	    return Sequence(
	            allowSpaces(String("++")), 
	            factor(),
	            push(new DOMUnaryExpression(DOMUnaryExpressionType.PrefixIncrement, (DOMExpression)pop())));
	}
	
	public Rule postfixIncrementExpression() {
	    return Sequence(                 
                factor(),
                allowSpaces(String("++")),
                push(new DOMUnaryExpression(DOMUnaryExpressionType.PostfixIncrement, (DOMExpression)pop())));
    }
	
	public Rule prefixDecrementExpression() {
	    return Sequence(
                allowSpaces(String("--")), 
                factor(),
                push(new DOMUnaryExpression(DOMUnaryExpressionType.PrefixDecrement, (DOMExpression)pop())));
    }
    
    public Rule postfixDecrementExpression() {
        return Sequence(                 
                factor(),
                allowSpaces(String("--")),
                push(new DOMUnaryExpression(DOMUnaryExpressionType.PostfixDecrement, (DOMExpression)pop())));
    }
    
    public Rule plusSignExpression() {
        return Sequence(
                allowSpaces(String("+")), 
                factor(),
                push(new DOMUnaryExpression(DOMUnaryExpressionType.PlusSign, (DOMExpression)pop())));
    }
    
    public Rule minusSignExpression() {
        return Sequence(
                allowSpaces(String("-")), 
                factor(),
                push(new DOMUnaryExpression(DOMUnaryExpressionType.MinusSign, (DOMExpression)pop())));
    }
    
    public Rule notExpression() {
        return Sequence(
                allowSpaces(String("!")), 
                factor(),
                push(new DOMUnaryExpression(DOMUnaryExpressionType.Not, (DOMExpression)pop())));
    }
	
	public Rule factor() {
	    return FirstOf(
	            explicitCastExpression(),
	            parensExpression(),
	            literal(),
	            functionCallExpression(),
	            variableReference());
	}
	
	public Rule explicitCastExpression() {
	    Var<DOMTypeReference> typeReference = new Var<DOMTypeReference>(); 
	    Var<DOMExpression> expression = new Var<DOMExpression>();
	    return Sequence(
	            OPEN_PARENTHESIS,
	            Sequence(
	                    namedTypeReference(),
	                    ACTION(typeReference.set(popTypeReference()))),
	            CLOSE_PARENTHESIS,
	            Sequence(
	                    expression(),
	                    ACTION(expression.set(popExpression()))),
	            push(new DOMExplicitCastExpression(typeReference.get(), expression.get())));
	}
	
	public Rule variableReference() {
	    return allowSpaces(
	            Sequence(
	                    name(),
	                    push(new DOMVariableReferenceExpression(match()))));
	}
	
	public Rule functionCallExpression() {
	    Var<FunctionCallBuilder> builder = new Var<FunctionCallBuilder>(new FunctionCallBuilder());	    
	    return Sequence(
	            allowSpaces(
	                    Sequence(
	                            name(),
	                            ACTION(builder.get().setFunctionName(match())))),
	            OPEN_PARENTHESIS,
	            Optional(
	                    expression(),
	                    ACTION(builder.get().appendArgument(popExpression())),
	                    ZeroOrMore(
	                            Sequence(
	                                    COMMA,
	                                    expression(),
	                                    ACTION(builder.get().appendArgument(popExpression())))
	                            )),
	            CLOSE_PARENTHESIS,
	            push(builder.get().build()));
	}
	
	public Rule name() {
	    return OneOrMore(
	            FirstOf(
	                    CharRange('a', 'z'),
	                    CharRange('A', 'Z')));
	}
	
	public Rule literal() {
		return FirstOf(
				boolLiteral(),
				doubleLiteral(),
				intLiteral());
	}
	
	public Rule intLiteral() {
		return allowSpaces(
				Sequence(
						OneOrMore(CharRange('0', '9')),
						push(new DOMLiteralExpression(DOMLiteralType.Int, match()))));
	}
	
	public Rule doubleLiteral() {
		return allowSpaces(
				Sequence(
						FirstOf(
								Sequence(".", OneOrMore(CharRange('0', '9'))),
								Sequence(OneOrMore(CharRange('0', '9')), ".", OneOrMore(CharRange('0', '9'))),
								Sequence(OneOrMore(CharRange('0', '9')), ".")),
						push(new DOMLiteralExpression(DOMLiteralType.Double, match()))));
	}
	
	public Rule boolLiteral() {
		return allowSpaces(
				Sequence(
						FirstOf("true", "false"),
						push(new DOMLiteralExpression(DOMLiteralType.Bool, match()))));
	}
		
	public Rule space() {
		return ZeroOrMore(
		        FirstOf(
		                singleLineComment(),
		                multilineComment(),
		                AnyOf(" \t\n")));
	}
	
	public Rule singleLineComment() {
	    return Sequence(
	            "//", 
	            ZeroOrMore(TestNot(AnyOf("\r\n")), ANY), 
	            FirstOf("\r\n", '\r', '\n', EOI));
	}
	
	public Rule multilineComment() {
	    return Sequence(
	            "/*", 
	            ZeroOrMore(TestNot("*/"), ANY), 
	            "*/");
	}
	
	public Rule allowSpaces(Rule rule) {
		return Sequence(space(), rule, space());
	}
	
	public Rule TERMINAL(String s) {
	    return allowSpaces(String(s));
	}
	
	private static class Helper {
		public static DOMBinaryExpression domBinaryExpressionFromString(
				String operation, 
				DOMExpression leftExpression, 
				DOMExpression rightExpression) {
			
			DOMBinaryExpressionType expressionType = null;
			
			if(operation.equals("+")) {
				expressionType = DOMBinaryExpressionType.Add;
			} else if(operation.equals("-")) {
				expressionType = DOMBinaryExpressionType.Sub;
			} else if(operation.equals("*")) {
				expressionType = DOMBinaryExpressionType.Mul;
			} else if(operation.equals("/")) {
				expressionType = DOMBinaryExpressionType.Div;
			} else if(operation.equals("<")) {
			    expressionType = DOMBinaryExpressionType.Less;
			} else if(operation.equals("<=")) {
			    expressionType = DOMBinaryExpressionType.LessOrEqual;
			} else if(operation.equals(">")) {
			    expressionType = DOMBinaryExpressionType.Greater;
			} else if(operation.equals(">=")) {
			    expressionType = DOMBinaryExpressionType.GreaterOrEqual;
			} else if(operation.equals("!=")) {
			    expressionType = DOMBinaryExpressionType.NotEqual;
			} else if(operation.equals("==")) {
			    expressionType = DOMBinaryExpressionType.Equal;
			} else if(operation.equals("&&")) {
                expressionType = DOMBinaryExpressionType.And;
			} else if(operation.equals("||")) {
                expressionType = DOMBinaryExpressionType.Or;
			} else if(operation.equals("=")) {
			    expressionType = DOMBinaryExpressionType.Assignment;
			} else if(operation.equals("+=")) {
                expressionType = DOMBinaryExpressionType.AddAndAssign;
			} else if(operation.equals("-=")) {
                expressionType = DOMBinaryExpressionType.SubAndAssign;
			} else if(operation.equals("*=")) {
                expressionType = DOMBinaryExpressionType.MulAndAssign;
			} else if(operation.equals("/=")) {
                expressionType = DOMBinaryExpressionType.DivAndAssign;
			}
                
			if(expressionType == null) {
				throw new RuntimeException(String.format("Unknown operation - %s", operation));
			}
			
			return new DOMBinaryExpression(expressionType, leftExpression, rightExpression);
		}
	}
	
	DOMFunctionDefinition popFunctionDefinition() {
        return (DOMFunctionDefinition)pop();
    }
    
    DOMTypeReference popTypeReference() {
        return (DOMTypeReference)pop();
    }
    
    DOMParameterDefinition popParameterDefinition() {
        return (DOMParameterDefinition)pop();
    }
    
    DOMStatement popStatement() {
        return (DOMStatement)pop();
    }
    
    DOMExpression popExpression() {
        return (DOMExpression)pop();
    }
	
	public static class FunctionCallBuilder {
	    private String functionName;
	    private final List<DOMExpression> arguments = new ArrayList<DOMExpression>();
	    
	    public boolean setFunctionName(String functionName) {
	        this.functionName = functionName;
	        return true;
	    }
	    
	    public boolean appendArgument(DOMExpression argument) {
	        arguments.add(argument);
	        return true;
	    }
	    
	    public DOMFunctionCallExpression build() {
	        if(functionName == null || functionName == "") {
	            throw new RuntimeException("Function name can't be empty");
	        }
	        
	        return new DOMFunctionCallExpression(functionName, arguments);
	    }
	}
	
	public static class CompositeStatementBuilder {
	    private final List<DOMStatement> statements = new ArrayList<DOMStatement>();
	    
	    public boolean appendStatement(DOMStatement domStatement) {
	        statements.add(domStatement);
	        return true;
	    }
	    
	    public DOMCompositeStatement build() {
	        return new DOMCompositeStatement(statements);
	    }
	}
	
	public static class IfStatementBuilder {
	    private DOMExpression conditionExpression;
	    private DOMStatement trueBranch;
	    private DOMStatement falseBranch;
	    
	    public boolean setConditionExpression(DOMExpression conditionExpression) {
	        this.conditionExpression = conditionExpression;
	        return true;
	    }
	    
	    public boolean setTrueBranch(DOMStatement trueBranch) {
	        this.trueBranch = trueBranch;
	        return true;
	    }
	    
	    public boolean setFalseBranch(DOMStatement falseBranch) {
            this.falseBranch = falseBranch;
            return true;
        }
	    
	    public DOMIfStatement build() {
	        return new DOMIfStatement(conditionExpression, trueBranch, falseBranch);
	    }
	}
	
	public static class ForStatementBuilder {
	    private DOMStatement initializerStatement;
	    private DOMExpression conditionExpression;
	    private DOMStatement stepStatement;
	    private DOMStatement bodyStatement;
	    
	    public boolean setInitializerStatement(DOMStatement initializerStatement) {
	        this.initializerStatement = initializerStatement;
	        return true;
	    }
	    
	    public boolean setConditionExpression(DOMExpression conditionExpression) {
            this.conditionExpression = conditionExpression;
            return true;
        }
	    
	    public boolean setStepStatement(DOMStatement stepStatement) {
            this.stepStatement = stepStatement;
            return true;
        }
	    
	    public boolean setBodyStatement(DOMStatement bodyStatement) {
            this.bodyStatement = bodyStatement;
            return true;
        }
	    
	    public DOMForStatement build() {
	        return new DOMForStatement(
	                initializerStatement, 
	                conditionExpression, 
	                stepStatement, 
	                bodyStatement);
	    }
	}
	
	public static class WhileStatementBuilder {
        private DOMExpression conditionExpression;
        private DOMStatement bodyStatement;
               
        public boolean setConditionExpression(DOMExpression conditionExpression) {
            this.conditionExpression = conditionExpression;
            return true;
        }
        
        public boolean setBodyStatement(DOMStatement bodyStatement) {
            this.bodyStatement = bodyStatement;
            return true;
        }
        
        public DOMWhileStatement build() {
            return new DOMWhileStatement(
                    conditionExpression, 
                    bodyStatement);
        }
    }
	
	public static class DoWhileStatementBuilder {
        private DOMExpression conditionExpression;
        private DOMStatement bodyStatement;
               
        public boolean setConditionExpression(DOMExpression conditionExpression) {
            this.conditionExpression = conditionExpression;
            return true;
        }
        
        public boolean setBodyStatement(DOMStatement bodyStatement) {
            this.bodyStatement = bodyStatement;
            return true;
        }
        
        public DOMDoWhileStatement build() {
            return new DOMDoWhileStatement(
                    conditionExpression, 
                    bodyStatement);
        }
    }
	
	public static class ReturnStatementBuilder {
        private DOMExpression expression;
               
        public boolean setExpression(DOMExpression conditionExpression) {
            this.expression = conditionExpression;
            return true;
        }
        
        public DOMReturnStatement build() {
            return new DOMReturnStatement(expression);
        }
    }
	
	public static class VariableDefinitionStatementBuilder {
	    private DOMTypeReference typeReference;
	    private String variableName;
	    private DOMExpression expression;
	    
	    public boolean setTypeReference(DOMTypeReference typeReference) {
	        this.typeReference = typeReference;
	        return true;
	    }
	    
	    public boolean setVariableName(String variableName) {
	        this.variableName = variableName;
	        return true;
	    }
	    
	    public boolean setExpression(DOMExpression expression) {
	        this.expression = expression;
	        return true;
	    }
	    
	    public DOMVariableDefinitionStatement build() {
	        return new DOMVariableDefinitionStatement(
	                typeReference, 
	                variableName, 
	                expression);
	    }
	}
	
	public static class ParameterDefinitionBuilder {
	    private DOMTypeReference typeReference;
	    private String parameterName;
	    
	    public boolean setTypeReference(DOMTypeReference typeReference) {
	        this.typeReference = typeReference;
	        return true;
	    }
	    
	    public boolean setParameterName(String parameterName) {
	        this.parameterName = parameterName;
	        return true;
	    }
	    
	    public DOMParameterDefinition build() {
	        return new DOMParameterDefinition(parameterName, typeReference);
	    }
	}
	
	public static class FunctionDefinitionBuilder {
	    private DOMTypeReference resultType;
	    private String functionName;
	    private List<DOMParameterDefinition> parameterDefinitions = new ArrayList<DOMParameterDefinition>();
	    private DOMStatement body;
	    
	    public boolean setResultType(DOMTypeReference typeReference) {
	        this.resultType = typeReference;
	        return true;
	    }
	    
	    public boolean setFunctionName(String functionName) {
	        this.functionName = functionName;
	        return true;
	    }
	    
	    public boolean appendParameterDefinition(DOMParameterDefinition parameterDefinition) {
	        parameterDefinitions.add(parameterDefinition);
	        return true;
	    }
	    
	    public boolean setBody(DOMStatement body) {
	        this.body = body;
	        return true;
	    }
	    
	    public DOMFunctionDefinition build() {
	        return new DOMFunctionDefinition(functionName, resultType, parameterDefinitions, body);
	    }
	}
	
	public static class ProgramBuilder {
	    private final List<DOMFunctionDefinition> functionDefinitions = new ArrayList<DOMFunctionDefinition>();
	    
	    public boolean appendFunctionDefinition(DOMFunctionDefinition domFunctionDefinition) {
	        functionDefinitions.add(domFunctionDefinition);
	        return true;
	    }
	    
	    public DOMProgram build() {
	        return new DOMProgram(functionDefinitions);
	    }
	}
}