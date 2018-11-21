package es.cnmc.everest.util;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dromera on 11/12/2017.
 */
@Component
public class DateUtil {

    public boolean isInRange(Date date, DateRange dateRange) {
        return (date.getTime() >= dateRange.getDateIni().getTime())
                && ((dateRange.getDateFin() == null) || (date.getTime() <= dateRange.getDateFin().getTime()));
    }


    public class DateRange {

        private Date dateIni = null;
        private Date dateFin = null;

        public DateRange(String dateIniStr, String dateFinStr, String dateFormat) throws ParseException {
            assert (dateIniStr!=null);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
            simpleDateFormat.setLenient(false);

            this.dateIni = simpleDateFormat.parse(dateIniStr);

            if ((dateFinStr!=null) && !dateFinStr.isEmpty()) {
                this.dateFin = simpleDateFormat.parse(dateFinStr);;
            }
        }

        public DateRange(Date dateIni, Date dateFin) {
            assert (dateIni!=null);
            this.dateIni = dateIni;
            this.dateFin = dateFin;
        }

        public Date getDateIni() {
            return dateIni;
        }

        public void setDateIni(Date dateIni) {
            this.dateIni = dateIni;
        }

        public Date getDateFin() {
            return dateFin;
        }

        public void setDateFin(Date dateFin) {
            this.dateFin = dateFin;
        }

        @Override
        public String toString() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            simpleDateFormat.setLenient(false);

            return "DateRange{" +
                    "dateIni=" + simpleDateFormat.format(dateIni) +
                    ", dateFin=" + simpleDateFormat.format(dateFin) +
                    '}';
        }
    }

}
