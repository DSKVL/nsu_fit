namespace Tests;

using Microsoft.VisualStudio.TestTools.UnitTesting;

using Nsu.ColiseumProblem.Contracts;
using Nsu.ColiseumProblem.Contracts.Cards;
using Strategy;

class ColorSplitShuffler : IDeckShuffler {
	public (Card[], Card[])	Shuffle() =>
		(Enumerable.Repeat(new Card(CardColor.Red), 18).ToArray(),
		 Enumerable.Repeat(new Card(CardColor.Black), 18).ToArray());
}

[TestClass]
public class UnitTest1
{
    [TestMethod]
    public void TestMethod1()
    {
			
			ICardPickStrategy player1 = new Trivial();
			ICardPickStrategy player2= new Trivial();

    	ColliseumSandbox sbx = new ColliseumSandbox(new ColorSplitShuffler(), player1, player2);
		}
}