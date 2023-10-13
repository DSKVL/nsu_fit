namespace ColliseumRunner;

using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.DependencyInjection;
using MassTransit;
using Nsu.ColiseumProblem.Contracts;
using Strategy;

public delegate Async.ICardPickStrategyAsync AsyncPlayerResolver(string name);
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
        services.AddSingleton<IColliseumSandbox, Async.ColliseumSandbox>();
				services.AddSingleton<RandomDeckShuffler>();
				services.AddDbContext<ExperimentalConditionsContext>();
        services.AddScoped<IDeckShuffler, DBDeckShuffler>();
			
      	services.AddTransient<Trivial>();      
				services.AddHttpClient("elon", c =>
						{
							c.BaseAddress = new Uri($"http://localhost:{hostContext.Configuration["p1"]}/strategy");
						});
				services.AddTransient<Async.FirstPlayerService>();
				services.AddHttpClient("zucc", c =>
						{
							c.BaseAddress = new Uri($"http://localhost:{hostContext.Configuration["p2"]}/strategy");
						});
				services.AddTransient<Async.SecondPlayerService>();

				services.AddMassTransit(x => 
						{
							x.UsingRabbitMq((context, cfg) =>
    					{
        				cfg.ConfigureEndpoints(context);
    					});
						});
				
				services.AddTransient<AsyncPlayerResolver>(serviceProvider => name =>
				{
					switch(name)
					{
						case "Elon": return serviceProvider.GetService<Async.FirstPlayerService>();
						case "Zucc": return serviceProvider.GetService<Async.SecondPlayerService>();
						default: return serviceProvider.GetService<Async.FirstPlayerService>();
					}
				});
			});
}
