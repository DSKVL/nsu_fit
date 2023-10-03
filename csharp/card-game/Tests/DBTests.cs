namespace Tests;

using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.EntityFrameworkCore;
using Xunit;
using ColliseumRunner;
using Nsu.ColiseumProblem.Contracts.Cards;

public class DBDeckShufflerTests 
{
	[Fact]
	public void DBDeckShufflerTest1() 
	{
		var rds = new RandomDeckShuffler();
		var configuration = new ConfigurationBuilder()
    	.AddInMemoryCollection(new Dictionary<string, string?>() {
						{"memdb", "true"},
						{"read", "true"},
						{"write", "true"}
					})
    	.Build();	
		var db = new ExperimentalConditionsContext(configuration);
		db.Database.OpenConnection();
		db.Database.EnsureDeleted();
		db.Database.EnsureCreated();
		db.SaveChanges();

		var serviceProvider = new ServiceCollection()
    	.AddLogging()
    	.BuildServiceProvider();

		var factory = serviceProvider.GetService<ILoggerFactory>();
		var logger = factory.CreateLogger<DBDeckShuffler>();
		
		var dbds = new DBDeckShuffler(rds, db, configuration, logger);

		var conditions = new List<(Card[], Card[])>();
		for (int i = 0; i < 100; i++) 
		{
			conditions.Add(dbds.Shuffle());
		}
	
		var dbds1 = new DBDeckShuffler(rds, db, configuration, logger);
		for (int i = 0; i < 100; i++) 
		{
			(Card[] sd1, Card[] sd2) = conditions[i];
			(Card[] d1, Card[] d2) = dbds1.Shuffle();
			Assert.Equivalent(sd1, d1);
			Assert.Equivalent(sd2, d2);
		}
	}
}
