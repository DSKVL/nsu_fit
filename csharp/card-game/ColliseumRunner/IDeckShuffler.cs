namespace ColliseumRunner;

using Nsu.ColiseumProblem.Contracts.Cards;

public interface IDeckShuffler 
{
	(Card[], Card[]) Shuffle();
}
