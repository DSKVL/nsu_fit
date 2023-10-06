namespace PlayerWebService;

using Nsu.ColiseumProblem.Contracts.Cards;

/*
class DeckConverter : TypeConverter {
	public override bool CanConvertFrom(ITypeDescriptorContext? context, Type sourceType) 
		=> (sourceType == typeof(string)) ? true : base.CanConvertFrom(context, sourceType);
	
		public override object ConvertFrom(ITypeDescriptorContext? context,
			System.Globalization.CultureInfo? culture, object value)
  {
  	if (	 value != null 
				&& value is string s 
				&& s.Any(c => c == 'r' || c == 'b'))
      return new DeckConverter().ConvertFrom(s);

    return base.ConvertFrom(context, culture, value);
  }
} 
*/

public class DeckDTO
{
	public Card[] deck;
}
