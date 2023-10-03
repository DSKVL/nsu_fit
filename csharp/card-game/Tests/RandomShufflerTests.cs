namespace Tests;

using Xunit;
using ColliseumRunner;
using Nsu.ColiseumProblem.Contracts.Cards;

public class RandomDeckShufflerTests 
{
	[Fact]
	public void ShufflerTest() 
	{
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
}
