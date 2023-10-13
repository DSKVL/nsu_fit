namespace StubServer.Data;

public record Parameter
{ 
 	public string name { get; set; }
	public ParameterType type { get; set; }
	public ParameterValue value { get; set; }
	public ParameterRange range { get; set; }
}

/*	Домен:
 *	Множественные диапазоны, 
 *	дискретные значения,
 *	Множественные диапазоны с шагом
 *
 *	Типы: 
 *	инт
 *	дублы
 *	Строки, домен произвольный
 *	Произвольный джсон
 * */

