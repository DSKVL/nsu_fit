namespace ColliseumRunner;

using Nsu.ColiseumProblem.Contracts;
using Nsu.ColiseumProblem.Contracts.Cards;

public class ColliseumSandbox : IColliseumSandbox
{
	private readonly IDeckShuffler _deckShuffler;
	private readonly ICardPickStrategy _player1, _player2;

	public ColliseumSandbox(IDeckShuffler deckShuffler,
			ICardPickStrategy player1, ICardPickStrategy player2) 
	{
		_deckShuffler = deckShuffler;
		_player1 = player1;
		_player2 = player2;
	}
	
	public ColliseumSandbox(IDeckShuffler deckShuffler,
			PlayerResolver resolver) : this(deckShuffler, resolver("Elon"), resolver("Zucc"))
	{}

	public bool runExperiment() 
	{
		(Card[] deck1, Card[] deck2) = _deckShuffler.Shuffle();
		var pick1 = _player1.Pick(deck1);
		var pick2 = _player2.Pick(deck2);
		Console.WriteLine($"p1 {pick1} p2 {pick2}");
		return deck2[pick1] == deck1[pick2];
	}
}
