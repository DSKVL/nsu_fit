using MassTransit;
using Microservice.Consumers;
using Microservice.Controllers;

var builder = WebApplication.CreateBuilder(args);

IConfiguration configuration = builder.Configuration;

builder.Services.AddControllers();
builder.Services.AddSingleton<StrategyController>();
builder.Services.AddSingleton<PickMessageConsumer>();
builder.Services.AddSingleton<DeckMessageConsumer>();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddMassTransit(x =>
	{
		x.AddConsumer<DeckMessageConsumer>();
		x.AddConsumer<PickMessageConsumer>();
		x.UsingRabbitMq((context, cfg) =>
			{
				cfg.ReceiveEndpoint($"{configuration["source"]}-deck", e =>
						e.ConfigureConsumer<DeckMessageConsumer>(context));
				cfg.ReceiveEndpoint($"{configuration["source"]}-pick", e =>
						e.ConfigureConsumer<PickMessageConsumer>(context));
				cfg.ConfigureEndpoints(context);
    	});	
		});

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();
