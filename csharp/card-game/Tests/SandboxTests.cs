namespace Tests;

using Xunit;
using ColliseumRunner;
using Nsu.ColiseumProblem.Contracts.Cards;
using Strategy;

file class CountShuffler : IDeckShuffler 
{
	public int count { get; set; } = 0;

	public CountShuffler() {}

	public (Card[], Card[]) Shuffle() 
	{
		count += 1;	
		return new RandomDeckShuffler().Shuffle();
	}
}

file class SplitShuffler : IDeckShuffler 
{
	public (Card[], Card[]) Shuffle() 
		=> (Enumerable.Repeat(new Card(CardColor.Red), 18).ToArray(),
		  	Enumerable.Repeat(new Card(CardColor.Black), 18).ToArray());
}

public class SandboxTests 
{
  [Fact]
  public void SandboxTest()
  {
		var trivial = new Trivial();
		var sbx = new ColliseumSandbox(new SplitShuffler(), trivial, trivial);

		for (int i = 0; i < 100; i++)
			Assert.False(sbx.runExperiment());

		var countShuffler = new CountShuffler();
		new ColliseumSandbox(countShuffler, trivial, trivial).runExperiment();	
		Assert.Equal(1, countShuffler.count);
	}
}
