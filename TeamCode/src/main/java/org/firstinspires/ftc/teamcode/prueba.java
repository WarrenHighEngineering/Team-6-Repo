package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Mecanum Drive HD Hex V2", group = "Linear OpMode")
public class MecanumDriveV2 extends LinearOpMode {

    // Definir los 4 motores HD Hex
    private DcMotor frontLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backLeft = null;
    private DcMotor backRight = null;

    @Override
    public void runOpMode() {
        // 1. Mapeo de hardware con los nombres configurados en el Robot Controller
        frontLeft  = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft   = hardwareMap.get(DcMotor.class, "backLeft");
        backRight  = hardwareMap.get(DcMotor.class, "backRight");

        // 2. Invertir la dirección de un lado (generalmente el izquierdo)
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        telemetry.addData("Estado", "Inicializado - Listo para ejecutar");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // 3. Capturar las entradas del control (Gamepad)
            // Se invierte Y porque en las palancas hacia arriba es un valor negativo en FTC
            double y  = -gamepad1.left_stick_y;            // Avanzar / Retroceder
            double x  =  gamepad1.left_stick_x * 1.1;      // Desplazamiento lateral (multiplicado para compensar fricción)
            double rx =  gamepad1.right_stick_x;           // Giro

            // 4. Ecuaciones cinemáticas estándar para chasis Mecanum
            double frontLeftPower  = y + x + rx;
            double backLeftPower   = y - x + rx;
            double frontRightPower = y - x - rx;
            double backRightPower  = y + x - rx;

            // 5. Normalizar las potencias si exceden el rango [-1.0, 1.0]
            double maxPower = Math.max(
                    Math.abs(frontLeftPower),
                    Math.max(Math.abs(backLeftPower),
                            Math.max(Math.abs(frontRightPower), Math.abs(backRightPower)))
            );

            if (maxPower > 1.0) {
                frontLeftPower  /= maxPower;
                backLeftPower   /= maxPower;
                frontRightPower /= maxPower;
                backRightPower  /= maxPower;
            }

            // 6. Aplicar las potencias calculadas a los motores
            frontLeft.setPower(frontLeftPower);
            backLeft.setPower(backLeftPower);
            frontRight.setPower(frontRightPower);
            backRight.setPower(backRightPower);

            // Telemetría para monitoreo
            telemetry.addData("Potencia [FL, FR]", "%.2f, %.2f", frontLeftPower, frontRightPower);
            telemetry.addData("Potencia [BL, BR]", "%.2f, %.2f", backLeftPower, backRightPower);
            telemetry.update();
        }
    }
}

