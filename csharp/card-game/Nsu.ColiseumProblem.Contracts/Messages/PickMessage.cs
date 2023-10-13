using MassTransit;

namespace Nsu.ColiseumProblem.Contracts.Messages;

[EntityName("pick")]
public record PickMessage(long id, PickSource source, int pick) {}
