package io.softwarestrategies.keeponme.common.data.value;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class DateRange {

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public DateRange(String dateRangeDescriptor, LocalDate externalStartDate, LocalDate externalEndDate) {
        if ("custom".equalsIgnoreCase(dateRangeDescriptor)) {
            if (externalStartDate == null) {
                throw new RuntimeException("There must be a Start date.");
            }
            else {
                startDate = LocalDateTime.from( externalStartDate.atTime(0,0,0) );
            }

            endDate = (externalEndDate == null) ?
                    LocalDateTime.from( LocalDate.now().atTime(23,59,59)) :
                    LocalDateTime.from( externalEndDate.atTime(23,59,59));

            if (endDate.isBefore(startDate)) {
                throw new RuntimeException("End Date cannot be before Start Date");
            }
        }
        else {
            startDate = LocalDateTime.from( LocalDate.now().atTime(0,0,0));
            endDate = LocalDateTime.from( LocalDate.now().atTime(23,59,59));

            if ("today".equalsIgnoreCase(dateRangeDescriptor)) {
                // Placeholder
            }
            else if ("last7days".equalsIgnoreCase(dateRangeDescriptor)) {
                startDate = startDate.minusDays(6);
            }
            else if ("last30days".equalsIgnoreCase(dateRangeDescriptor)) {
                startDate = startDate.minusDays(29);
            }
            else {
                throw new RuntimeException("Invalid dateRangeDescriptor: " + dateRangeDescriptor);
            }
        }
    }
}
