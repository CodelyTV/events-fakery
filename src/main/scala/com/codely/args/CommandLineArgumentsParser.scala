package com.codely.args

import scopt.{OParser, OParserBuilder}

object CommandLineArgumentsParser {
  private val builder: OParserBuilder[CommandLineArguments] =
    OParser.builder[CommandLineArguments]

  private val argsParser: OParser[Unit, CommandLineArguments] = {
    import builder._
    OParser.sequence(
      programName("events-generator"),
      opt[String]('c', "eventsConfigPath")
        .required()
        .valueName("<archivo>")
        .action((x, c) => c.copy(eventsConfigPath = x))
        .text("path to required configuration file"),
      help("help").text(
        "To properly run the application, please provide the path to the event configuration file using the argument:" +
          "'--eventsConfigPath <path>'. This is essential for loading the initial configuration required for the " +
          " application to function."
      )
    )
  }

  def parse(args: Array[String]): CommandLineArguments = {
    OParser.parse(argsParser, args, CommandLineArguments()) match {
      case Some(config) => config
      case _ =>
        System.err.println(
          "Error: Unable to parse command line arguments. Use '--help' for more information."
        )
        sys.exit(
          1
        )
    }
  }

}
