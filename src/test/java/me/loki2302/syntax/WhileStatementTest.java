package me.loki2302.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.loki2302.syntax.ParseResult;
import me.loki2302.syntax.expectations.parser.ParseResultExpectation;
import me.loki2302.syntax.parser.ExpressionParser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static me.loki2302.syntax.expectations.ParserTestDsl.*;

@RunWith(Parameterized.class)
public class WhileStatementTest {
	private final String expression;
	private final ExpressionParser parser;	
	private final ParseResultExpectation parseResultExpectation;
	
	public WhileStatementTest(			
			String expression,
			ExpressionParser parser,
			ParseResultExpectation parseResultExpectation) {
		this.expression = expression;
		this.parser = parser;		
		this.parseResultExpectation = parseResultExpectation;
	}
	
	@Parameters(name = "#{index}: Parse \"{0}\"")
	public static Collection<Object[]> makeTestData() {
		List<Object[]> parameters = new ArrayList<Object[]>();
		
		parameters.add(new Object[] { " while ( 1 ) 2; ", parsePureStatement(), result(               
                isStatement(isWhileStatement(
                        whileHasConditionExpression(isIntLiteralWithValueOf("1")),
                        whileHasBody(isExpressionStatement(withExpression(isIntLiteralWithValueOf("2"))))))
                ) });
		
		return parameters;
	}
	
	@Test
	public void testBinaryExpressionParseResult() {
		ParseResult parseResult = parser.parse(expression);
		parseResultExpectation.check(parseResult);
	}	
}