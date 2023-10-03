namespace Tests;

using Xunit;
using Strategy;
using ColliseumRunner;
using Nsu.ColiseumProblem.Contracts.Cards;

file class SplitShuffler : IDeckShuffler {
	public (Card[], Card[]) Shuffle() {
		return (Enumerable.Repeat(new Card(CardColor.Red), 18).ToArray(),
						Enumerable.Repeat(new Card(CardColor.Black), 18).ToArray());
	}
}

public class TrivialStrategyTests
{
	
	[Fact]
	public void TrivialStrategyTest() {
		var trivial = new Trivial();
		var shuffler = new RandomDeckShuffler();
		(var c1, var c2) = shuffler.Shuffle();
		Assert.Equal(0, trivial.Pick(c1));
		Assert.Equal(0, trivial.Pick(c2));
	}
}