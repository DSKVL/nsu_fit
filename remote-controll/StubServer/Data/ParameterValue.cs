namespace StubServer.Data;

public abstract record ParameterValue 
{
	public Attributes attributes { get; set; } = new Attributes();
}

