import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Grab('com.xlson.groovycsv:groovycsv:1.3')
import static com.xlson.groovycsv.CsvParser.parseCsv

void parseData(String fileName, String insertHeader, List<String> columns) {
    File inputFile = new File('charging-station-data.csv')
    String csvContent = inputFile.getText('utf-8')
    Iterator csvFile = parseCsv(csvContent, separator: ',', readFirstLine: false)

    File outputFile = new File(fileName)


    outputFile.append(insertHeader)
    Set<String> stations = new TreeSet<>()
    for (line in csvFile) {

        StringBuilder builder = new StringBuilder("(")
        for(column in columns){
            String columnLine = line."$column"
            String value = setColumnValue(column, columnLine)
            appendFieldInCorrectFormat(builder, value)
            if(column.equalsIgnoreCase(columns.last())){
                builder.append(")")
            }
            else {
                builder.append(", ")
            }
        }
        stations.add(builder.toString())
    }

    for (element in stations) {
        if (stations.last().equalsIgnoreCase(element)) {
            outputFile.append(element + ";")
        } else {
            outputFile.append(element + ",")
        }
    }
}

String setColumnValue(String column ,String columnLine){
    if (columnLine.isEmpty()) {
        return "DEFAULT"
    } else if (column.contains("Date")) {
        return LocalDateTime.parse(
                columnLine.toString(),
                DateTimeFormatter.ofPattern("M/d/yyyy H:m")
        ).format("yyyy-MM-dd HH:mm")
    }
    return columnLine
}

void appendFieldInCorrectFormat(StringBuilder builder, String value){
    if(value.equalsIgnoreCase("default"))
        builder.append(value)
    else
        builder.append("'").append(value).append("'")
}



List<String> stations = Arrays.asList("Station Name", "MAC Address", "EVSE ID", "Org Name")

List<String> addresses = Arrays.asList("Address 1", "City", "County", "State/Province",
        "Postal Code", "Country", "Station Name")

List<String> ports = Arrays.asList("Port Number", "Port Type", "Plug Type")

List<String> events = Arrays.asList('Plug In Event Id', "Start Date", "End Date",
        "Total Duration (hh:mm:ss)", "Charging Time (hh:mm:ss)", "Energy (kWh)", "GHG Savings (kg)",
        "Gasoline Savings (gallons)", "Ended By", "User ID", "Transaction Date (Pacific Time)", "Fee", "Currency", "Station Name", "Port Number")


parseData("stations.sql",
        "INSERT INTO station(name,mac_address, evse_id, org_name)\nVALUES ", stations)

parseData("addresses.sql",
        "INSERT INTO address(street,city, county, state, postal_code, country, station_name)\nVALUES "
        , addresses)

parseData("ports.sql",
        "INSERT INTO ports_and_plugs(port_number, port_type, plug_type)\nVALUES ", ports)

parseData("events.sql",
        "INSERT INTO charging_event(plug_in_event_id, start_date, end_date, total_duration, " +
                "charging_time, energy, ghg_savings, gasoline_savings, ended_by, user_id, transaction_date, fee," +
                " currency, station_name, port)\nVALUES ", events)

