namespace ColliseumRunner;

using Nsu.ColiseumProblem.Contracts.Cards;
using System;

public class RandomDeckShuffler : IDeckShuffler 
{

	public (Card[], Card[]) Shuffle() 
	{
		var rng = new Random();
		var deck = Enumerable.Repeat(new Card(CardColor.Red), 18)
			.Concat(Enumerable.Repeat(new Card(CardColor.Black), 18))
			.ToArray();
		var shuffledDeck = deck.OrderBy(x => rng.Next()).ToArray();

		return (shuffledDeck.Take(18).ToArray(), 
						shuffledDeck.Reverse().Take(18).ToArray());
	}
}
