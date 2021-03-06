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
public class ProgramTest {
	private final String expression;
	private final ExpressionParser parser;	
	private final ParseResultExpectation parseResultExpectation;
	
	public ProgramTest(			
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
		
		parameters.add(new Object[] { " int suma ( ) return 1; double sumb ( ) return 3.14;", parseProgram(), result(               
                isProgram(
                        programHasFunctions(2),
                        programHasFunction(
                                0,
                                hasReturnType(isNamedTypeReference(withTypeName("int"))),
                                functionHasName("suma"),
                                functionHasNoParameters(),
                                hasBody(isReturn(hasExpression(isIntLiteralWithValueOf("1"))))),
                        programHasFunction(
                                1,
                                hasReturnType(isNamedTypeReference(withTypeName("double"))),
                                functionHasName("sumb"),
                                functionHasNoParameters(),
                                hasBody(isReturn(hasExpression(isDoubleLiteralWithValueOf("3.14"))))))
                ) });
		
		return parameters;
	}
	
	@Test
	public void testBinaryExpressionParseResult() {
		ParseResult parseResult = parser.parse(expression);
		parseResultExpectation.check(parseResult);
	}	
}