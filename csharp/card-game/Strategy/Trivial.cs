using Nsu.ColiseumProblem.Contracts;
using Nsu.ColiseumProblem.Contracts.Cards;

namespace Strategy;

public class Trivial : ICardPickStrategy {
	public int Pick(Card[] _) { return 0; }
} 
