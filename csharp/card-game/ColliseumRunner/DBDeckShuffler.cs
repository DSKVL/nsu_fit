namespace ColliseumRunner;

using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using Nsu.ColiseumProblem.Contracts.Cards;

public class DBDeckShuffler : IDeckShuffler {
	private int _currentExperimentId = 1;
	private RandomDeckShuffler _randomDeckShuffler;
	private readonly ExperimentalConditionsContext _db;
	private readonly ILogger<DBDeckShuffler> _logger;
	private bool _read = false;
	private bool _write = false;

	public DBDeckShuffler(
			RandomDeckShuffler randomDeckShuffler,
			ExperimentalConditionsContext db,
			IConfiguration configuration,
			ILogger<DBDeckShuffler> logger) 
	{
		_randomDeckShuffler = randomDeckShuffler;
		_db = db;
		db.Database.EnsureCreated();
		_logger = logger;
		_read = configuration["read"] != null;
		_write = configuration["write"] != null;
	}

	public (Card[], Card[]) Shuffle() 
	{
		if (_read) 
		{
			_logger.LogInformation("Getting experimental conditions with id {0}", _currentExperimentId);
			var ec = _db.Find<ExperimentalConditions>(_currentExperimentId++);
			
			if (ec != null)
				return (ec.deck1, ec.deck2);

			_logger.LogInformation("Experimental conditions with id {0} not found",_currentExperimentId);
		}

		(Card[] d1, Card[] d2) = _randomDeckShuffler.Shuffle();

		if (_write) 
		{
			var cond = new ExperimentalConditions(d1, d2);
			_db.ExperimentalConditions.Add(cond);		
			_db.SaveChanges();
			Console.WriteLine("Adding new experimental conditions with id {0}", cond.id);
		}

		return (d1, d2);	
	}
} 
