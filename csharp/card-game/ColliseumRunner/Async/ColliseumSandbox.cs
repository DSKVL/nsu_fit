using Nsu.ColiseumProblem.Contracts.Cards;

namespace ColliseumRunner.Async;

public class ColliseumSandbox : IColliseumSandbox
{
	private readonly ICardPickStrategyAsync _player1;
	private readonly ICardPickStrategyAsync _player2;
	private IDeckShuffler _deckShuffler;
		
	public ColliseumSandbox(IDeckShuffler deckShuffler,
												 ICardPickStrategyAsync player1,
												 ICardPickStrategyAsync player2)
	{
		_player1 = player1;
		_player2 = player2;
		_deckShuffler = deckShuffler;
	}
	
	public ColliseumSandbox(IDeckShuffler deckShuffler,
			AsyncPlayerResolver resolver) : this(deckShuffler, resolver("Elon"), resolver("Zucc"))
	{}

	public bool runExperiment() 	
	{
		(Card[] d1, Card[] d2) = _deckShuffler.Shuffle();	
		var pick1 = _player1.Pick(d2);
		var pick2 = _player2.Pick(d1);
		
		return pick1.Result == pick2.Result;	
	}
}


