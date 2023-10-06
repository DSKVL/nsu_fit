namespace ColliseumRunner;

using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.DependencyInjection;
using MassTransit;

using Nsu.ColiseumProblem.Contracts;
using Nsu.ColiseumProblem.Contracts.Messages;
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


				services.AddTransient<FirstPlayerConsumer>();
				services.AddTransient<SecondPlayerConsumer>();

				services.AddMassTransit(x => 
						{
							x.AddConsumer<FirstPlayerConsumer>(); 
							x.AddConsumer<SecondPlayerConsumer>();
							
							x.UsingRabbitMq((context, cfg) =>
    					{
								cfg.ReceiveEndpoint("picks", e => 
										{
											e.Bind<PickMessage>();
											e.ConfigureConsumer<FirstPlayerConsumer>(context);
											e.ConfigureConsumer<SecondPlayerConsumer>(context);
										});
        				cfg.ConfigureEndpoints(context);
    					});
						});
				
				services.AddTransient<PlayerResolver>(serviceProvider => name =>
				{
					switch(name)
					{
						case "Elon": return serviceProvider.GetService<FirstPlayerConsumer>();
						case "Zucc": return serviceProvider.GetService<SecondPlayerConsumer>();
						default: return serviceProvider.GetService<Trivial>();
					}
				});
			});
}
