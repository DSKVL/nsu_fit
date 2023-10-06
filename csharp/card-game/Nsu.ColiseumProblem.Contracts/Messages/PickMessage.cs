namespace Nsu.ColiseumProblem.Contracts.Messages;

public record PickMessage 
{
	public int id { get; init; }
	public int pick { get; init; }
}
