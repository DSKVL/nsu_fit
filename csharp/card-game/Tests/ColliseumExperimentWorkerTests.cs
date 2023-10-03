namespace Tests;

using Xunit;
using ColliseumRunner;
using Strategy;
using Nsu.ColiseumProblem.Contracts;
using Microsoft.Extensions.Configuration;

file class CounterSandbox : ColliseumSandbox 
{
	public int count { get; set; } = 0;

	public CounterSandbox(IDeckShuffler d_,
			ICardPickStrategy p1_, ICardPickStrategy p2_) : base(d_, p1_, p2_) {}

	override public bool runExperiment() {
		count++;
		return true;	
	}
}

public class ColliseumExperimentWorkerTestS
{

	[Fact]
	public void ColliseumExperimentWorkerTest() 
	{
		var trivial = new Trivial();
		var counter = new CounterSandbox(new RandomDeckShuffler(), trivial, trivial);

		var configuration = new ConfigurationBuilder()
    	.AddInMemoryCollection(new Dictionary<string, string?>())
    	.Build();
		var worker = new ColliseumExperimentWorker(counter, configuration);
		
		worker.runExperiment();
		Assert.Equal(1000000, counter.count);
	}
}
