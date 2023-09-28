using Nsu.ColiseumProblem.Contracts.Cards;

public class RandomDeckShuffler : IDeckShuffler 
{
	public (Card[], Card[]) Shuffle() 
	{
		var rng = new Random();
		var shuffledDeck = Enumerable.Repeat(new Card(CardColor.Red), 18)
			.Concat(Enumerable.Repeat(new Card(CardColor.Black), 18))
			.OrderBy(_ => rng.Next());
		return (shuffledDeck.Take(18).ToArray(), shuffledDeck.ToArray());
	}
}
