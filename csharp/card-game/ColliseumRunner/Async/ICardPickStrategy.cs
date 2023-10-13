using Nsu.ColiseumProblem.Contracts.Cards;

namespace ColliseumRunner.Async;

public interface ICardPickStrategyAsync
{
	Task<CardColor> Pick(Card[] deck);
}
