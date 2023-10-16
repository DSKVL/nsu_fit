namespace StubServer.Data.UInt32;

public record UInt32Parameter : Parameter
{
	public UInt32Value value { get; set; }
	public UInt32Range range { get; set; }
}
