using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;
using Microsoft.Extensions.Configuration;
using Nsu.ColiseumProblem.Contracts.Cards;

public class ExperimentalConditionsContext : DbContext 
{
	public DbSet<ExperimentalConditions> ExperimentalConditions { get; set; }
	public string DbPath { get; }
	public string connectionString;
	public ExperimentalConditionsContext(IConfiguration configuration) : base()
	{
		var folder = Environment.SpecialFolder.LocalApplicationData;
    var path = Environment.GetFolderPath(folder);
    DbPath = System.IO.Path.Join(path, "conditions.db");
		if (null != configuration["memdb"])
			connectionString = "DataSource=myshareddb;mode=memory;cache=shared";
		else 
			connectionString = $"Data Source={DbPath}";
	}
	protected override void OnConfiguring(DbContextOptionsBuilder options)
    => options.UseSqlite(connectionString);
	
	private DeckConverter converter = new DeckConverter();
	protected override void OnModelCreating(ModelBuilder modelBuilder)
	  => modelBuilder
				.Entity<ExperimentalConditions>(eb => 
						{
							eb.HasKey(ec => ec.id);
							eb.Property(ec => ec.deck1).HasColumnName("Deck1").HasConversion(converter);
							eb.Property(ec => ec.deck2).HasColumnName("Deck2").HasConversion(converter);
						});
}

public class ExperimentalConditions 
{
	public ExperimentalConditions() { deck1 = new Card[0]; deck2 = new Card[0]; }
	public ExperimentalConditions(Card[] d1, Card[] d2) 
	{
		deck1 = d1;
		deck2 = d2;
	}
	public int id { get; set; }
	public Card[] deck1 { get; set; }
	public Card[] deck2 { get; set; }
}

public class DeckConverter : ValueConverter<Card[], string> 
{
	public DeckConverter() : base(
		ca => String.Concat(ca.Select(c 
			=> c.Color == CardColor.Red ? 'r' : 'b')), 
		s => s.Select(c 
			=> new Card(c == 'r' ? CardColor.Red : CardColor.Black)).ToArray()) {}
}
