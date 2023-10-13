namespace StubServer.Data;

public record Attributes
{
	public bool read { get; set; } = true;
	public bool write { get; set; } = false;
}
