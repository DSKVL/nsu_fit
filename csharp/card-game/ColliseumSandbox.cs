using Nsu.ColiseumProblem.Contracts;
using Nsu.ColiseumProblem.Contracts.Cards;

public class ColliseumSandbox {
	private IDeckShuffler _deckShuffler;
	private ICardPickStrategy _player1, _player2;

	public ColliseumSandbox(IDeckShuffler deckShuffler,
			ICardPickStrategy player1, ICardPickStrategy player2) {
		_deckShuffler = deckShuffler;
		_player1 = player1;
		_player2 = player2;
	}

	public bool runExperiment() {
		(Card[] deck1, Card[] deck2) = _deckShuffler.Shuffle();
		return deck2[_player1.Pick(deck1)] == deck1[_player2.Pick(deck2)];
	}
}
