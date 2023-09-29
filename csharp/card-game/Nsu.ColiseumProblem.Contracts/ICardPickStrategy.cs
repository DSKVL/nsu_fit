using Nsu.ColiseumProblem.Contracts;
using Nsu.ColiseumProblem.Contracts.Cards;

namespace Nsu.ColiseumProblem.Contracts;

/// <summary>
/// Стратегия выбора карты
/// </summary>
public interface ICardPickStrategy
{
    /// <summary>
    /// Возвращает номер карты в стопке другой жертвы согласно алгоритма
    /// </summary>
    /// <param name="cards">Стопка карт</param>
    /// <returns>Номер карты начиная с 0</returns>
    public int Pick(Card[] cards);
}