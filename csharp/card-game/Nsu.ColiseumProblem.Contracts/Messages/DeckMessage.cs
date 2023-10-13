using Nsu.ColiseumProblem.Contracts.Cards;
using MassTransit;

namespace Nsu.ColiseumProblem.Contracts.Messages;

[EntityName("deck")]
public record DeckMessage (long id, Card[] deck) {}
