using Nsu.ColiseumProblem.Contracts.Cards;

namespace Nsu.ColiseumProblem.Contracts.Messages;

public record DeckMessage 
{
	public int id { get; init; }
	public Card[] deck { get; init; } = new Card[0];
}
