namespace StubServer.Data.UInt32;

public record UInt32Range : ParameterRange
{
	public System.UInt32 upperBound = System.UInt32.MaxValue;
	public System.UInt32 lowerBound = System.UInt32.MinValue;
}
