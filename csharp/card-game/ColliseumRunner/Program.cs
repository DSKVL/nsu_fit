namespace ColliseumRunner;

using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.DependencyInjection;
using Nsu.ColiseumProblem.Contracts;
using Strategy;

class Program
{
  public static void Main(string[] args)
  {
    CreateHostBuilder(args).Build().Run();
  }
    
	public static IHostBuilder CreateHostBuilder(string[] args)        	
  {
    return Host.CreateDefaultBuilder(args)
      .ConfigureServices((hostContext, services) =>
      {
			  services.AddHostedService<ColliseumExperimentService>();
        services.AddSingleton<ColliseumSandbox>();
				services.AddSingleton<RandomDeckShuffler>();
				services.AddDbContext<ExperimentalConditionsContext>();
        services.AddScoped<IDeckShuffler, DBDeckShuffler>();
      	services.AddScoped<ICardPickStrategy, Trivial>();       
			});
    }
}
