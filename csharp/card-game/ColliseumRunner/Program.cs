namespace ColliseumRunner;

using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Http;

using Nsu.ColiseumProblem.Contracts;
using Strategy;

public delegate ICardPickStrategy PlayerResolver(string name);

class Program
{
  public static void Main(string[] args)
  	=> CreateHostBuilder(args).Build().Run();
    
	public static IHostBuilder CreateHostBuilder(string[] args)        	
  	=> Host.CreateDefaultBuilder(args)
		.ConfigureServices((hostContext, services) =>
      {
			  services.AddHostedService<ColliseumExperimentService>();
        services.AddSingleton<ColliseumSandbox>();
				services.AddSingleton<RandomDeckShuffler>();
				services.AddDbContext<ExperimentalConditionsContext>();
        services.AddScoped<IDeckShuffler, DBDeckShuffler>();
			
      	services.AddTransient<Trivial>();      
				services.AddHttpClient("p1", c =>
						{
							c.BaseAddress = new Uri($"http://localhost:{hostContext.Configuration["p1"]}/strategy");
						});
				services.AddTransient<FirstPlayerService>();
				services.AddHttpClient("p2", c =>
						{
							c.BaseAddress = new Uri($"http://localhost:{hostContext.Configuration["p2"]}/strategy");
						});
				services.AddTransient<SecondPlayerService>();


				services.AddTransient<PlayerResolver>(serviceProvider => name =>
				{
					switch(name)
					{
						case "Elon": return serviceProvider.GetService<FirstPlayerService>();
						case "Zucc": return serviceProvider.GetService<SecondPlayerService>();
						default: return serviceProvider.GetService<Trivial>();
					}
				});
			});
}
