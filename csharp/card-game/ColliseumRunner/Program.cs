using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.DependencyInjection;
using Nsu.ColiseumProblem.Contracts;
using Strategy;

namespace Nsu.ColiseumProblem;

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
			  services.AddHostedService<ColliseumExperimentWorker>();
        services.AddSingleton<ColliseumSandbox>();
        services.AddScoped<IDeckShuffler, RandomDeckShuffler>();
      	services.AddScoped<ICardPickStrategy, Trivial>();       
			});
    }
}
