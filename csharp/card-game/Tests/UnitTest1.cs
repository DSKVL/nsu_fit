namespace Tests;

using Xunit;
using Strategy;
using ColliseumRunner;
using Nsu.ColiseumProblem.Contracts.Cards;
using Nsu.ColiseumProblem.Contracts;

class SplitShuffler : IDeckShuffler {
	public (Card[], Card[]) Shuffle() {
		return (Enumerable.Repeat(new Card(CardColor.Red), 18).ToArray(),
						Enumerable.Repeat(new Card(CardColor.Black), 18).ToArray());
	}
}

class CounterSandbox : ColliseumSandbox {
	public int count { get; set; } = 0;

	public CounterSandbox(IDeckShuffler d_,
			ICardPickStrategy p1_, ICardPickStrategy p2_) : base(d_, p1_, p2_) {}

	override public bool runExperiment() {
		count++;
		return true;	
	}
}

class CountShuffler : IDeckShuffler {
	public int count { get; set; } = 0;

	public CountShuffler() {}

	public (Card[], Card[]) Shuffle() {
		count += 1;	
		return new RandomDeckShuffler().Shuffle();
	}
}

public class UnitTest1
{
	ICardPickStrategy trivial = new Trivial();

  [Fact]
  public void SandboxTest()
  {
		var sbx = new ColliseumSandbox(new SplitShuffler(), trivial, trivial);

		for (int i = 0; i < 100; i++)
			Assert.False(sbx.runExperiment());

		var countShuffler = new CountShuffler();
		new ColliseumSandbox(countShuffler, trivial, trivial).runExperiment();	
		Assert.Equal(1, countShuffler.count);
  }
	
	[Fact]
	public void ColliseumExperimentWorkerTest() {
		var counter = new CounterSandbox(new RandomDeckShuffler(), trivial, trivial);
		var worker = new ColliseumExperimentWorker(counter);
		
		worker.runExperiment();
		Assert.Equal(1000000, counter.count);
	}

	[Fact]
	public void ShufflerTest() {
		var shuffler = new RandomDeckShuffler();
		(var c1, var c2) = shuffler.Shuffle();
		Assert.Equal(c1.Length, 18);
		Assert.Equal(c2.Length, 18);
		
		int reds = c1.Where(x => x.Color == CardColor.Red).Count()
									 + c2.Where(x => x.Color == CardColor.Red).Count();
		int blacks = c1.Where(x => x.Color == CardColor.Black).Count()
									 + c2.Where(x => x.Color == CardColor.Black).Count();

		Assert.Equal(18, reds);
		Assert.Equal(18, blacks);
	}
	
	[Fact]
	public void TrivialStrategyTest() {
		var shuffler = new RandomDeckShuffler();
		(var c1, var c2) = shuffler.Shuffle();
		Assert.Equal(0, trivial.Pick(c1));
		Assert.Equal(0, trivial.Pick(c2));
	}
}